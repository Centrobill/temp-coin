package service.app

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.ConnectionContext
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import service.rest.DataHttpService
import service.services.DataService
import service.utils.AppConfig

import scala.concurrent.Future

class DataApi(walletsCoordinator: ActorRef) extends RouteBinder {

  def start()(implicit cnf: AppConfig, system: ActorSystem, connectionContext: ConnectionContext, mat: ActorMaterializer): Future[ServerBinding] = {
    val dataService = new DataService(walletsCoordinator)
    val dataHttpService = new DataHttpService(dataService)
    bind(dataHttpService.route, cnf.dataHost, cnf.dataPort)
  }
}

object DataApi {
  def apply(walletsCoordinator: ActorRef): DataApi = new DataApi(walletsCoordinator)
}
