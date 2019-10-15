package service.app

import java.io.FileInputStream
import java.security.KeyStore

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.stream.{ActorMaterializer, TLSClientAuth}
import com.typesafe.sslconfig.akka.AkkaSSLConfig
import com.typesafe.sslconfig.ssl.ClientAuth
import javax.net.ssl._
import service.utils.{AppConfig, BuildInfo, InitializationException, Rnd}

import scala.concurrent.ExecutionContext
import scala.util.Try

trait ActorSystemApp {
  protected val signalHandler = AppSignalHandler()
  implicit val cnf: AppConfig = try AppConfig() catch {
    case t: Throwable => throw new IllegalArgumentException("Configuration error:" + t.getMessage, t)
  }
  implicit val system: ActorSystem = ActorSystem(cnf.actorSystemName, cnf.config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  def shutdownProcedure(): Unit

  val log: LoggingAdapter = system.log
  system.registerOnTermination(shutdownProcedure())
  signalHandler.registerReaper(shutdownProcedure())

  log.info("Actor system start: " + BuildInfo)

  sys.addShutdownHook {
    log.info("Actor system shutdown")
    system.terminate() //shutdown()
  }

  implicit val httpsContext: HttpsConnectionContext = Try {
    val akkaSSLConfig: AkkaSSLConfig = Http().sslConfig
    val configSettings = akkaSSLConfig.config
    // TRUSTED CA CERT LOADED FROM THE SAME JKS AS SERVER KEY PAIR
    val keyStoreConfig = configSettings.keyManagerConfig.keyStoreConfigs.headOption
      .getOrElse(throw InitializationException(detail = "'akka.ssl-config.keyManager.stores' not configured"))
    val keyStore = KeyStore.getInstance(keyStoreConfig.storeType)
    val ksIn = new FileInputStream(keyStoreConfig.filePath.getOrElse(throw InitializationException(detail = "'akka.ssl-config.keyManager.stores' keys file path not configured")))
    val keyPass = keyStoreConfig.password.map(_.toCharArray).orNull
    try keyStore.load(ksIn, keyPass) finally ksIn.close()
    val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    keyManagerFactory.init(keyStore, keyPass)

    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
    trustManagerFactory.init(keyStore)

    val sslContext: SSLContext = SSLContext.getInstance(configSettings.protocol)
    sslContext.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, Rnd.rng)

    val ciphersOpt = Some(scala.collection.immutable.Seq.empty[String] ++ akkaSSLConfig.configureCipherSuites(sslContext.getSupportedSSLParameters.getCipherSuites, configSettings).toSeq)
    val protocolsOpt = Some(scala.collection.immutable.Seq.empty[String] ++ akkaSSLConfig.configureProtocols(sslContext.getSupportedSSLParameters.getProtocols, configSettings).toSeq)
    val clientAuth = configSettings.sslParametersConfig.clientAuth match {
      case ClientAuth.Default => None
      case ClientAuth.Need => Some(TLSClientAuth.Need)
      case ClientAuth.Want => Some(TLSClientAuth.Want)
      case ClientAuth.None => Some(TLSClientAuth.None)
    }

    ConnectionContext.https(sslContext, Some(akkaSSLConfig), ciphersOpt, protocolsOpt, clientAuth)
  }.fold[HttpsConnectionContext](e => {
    log.error("Context initialization failed: {}", e.getMessage)
    system.terminate()
    throw e
  }, identity)

}