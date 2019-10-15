package service.services

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import service.core.WalletActor._
import service.core.WalletsCoordinator.{GetWalletStatuses, WalletMessage}
import service.model._
import service.model.currency.BlockChainCurrency
import service.rest.api.{CoinatorApiResponse, CoinatorDataResponse}
import service.utils.{AppConfig, BadRequestException}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

class DataService(walletCoordinator: ActorRef)(implicit val system: ActorSystem, cnf: AppConfig) {

  import system.dispatcher

  private implicit val defaultTimeout: Timeout = cnf.defaultTimeout.duration + 500.millis

  def getAddress(requestID: String, currency: BlockChainCurrency): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, GetNewAddress(requestID))).mapTo[AddressEntity].map(CoinatorDataResponse[AddressEntity])

  def getAddressStatus(requestID: String, currency: BlockChainCurrency, address: String, confirmationLimit: Option[String]): Future[CoinatorApiResponse] =
    Future(
      confirmationLimit.map(s => Try(s.toInt).getOrElse(throw BadRequestException(detail = "'confirmationLimit' parameter not a number")))
    )
      .flatMap((limit: Option[Int]) =>
        walletCoordinator.ask(WalletMessage(currency, GetAddressIncome(address, limit))).mapTo[AddressIncome]
          .map(CoinatorDataResponse[AddressIncome])
      )

  def initiateTransaction(requestID: String, transaction: Transaction): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(transaction.amount.currency, CreateTransaction(requestID, transaction.withRequestId(requestID))))
      .mapTo[Transaction].map(CoinatorDataResponse[Transaction])

  def getTransactionStatus(requestID: String, currency: BlockChainCurrency, transactionId: String): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, GetTransaction(transactionId))).mapTo[Transaction].map(CoinatorDataResponse[Transaction])

  def deleteTransaction(requestID: String, currency: BlockChainCurrency, transactionId: String): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, CancelTransaction(transactionId))).map(_ => CoinatorApiResponse.empty)

  def getWalletStatus(requestID: String, currency: BlockChainCurrency): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, GetWalletStatus)).mapTo[WalletStatus].map(CoinatorDataResponse[WalletStatus])

  def getWalletsStatuses: Future[CoinatorApiResponse] =
    walletCoordinator.ask(GetWalletStatuses).mapTo[List[WalletStatus]].map(CoinatorDataResponse[List[WalletStatus]])

  def setWalletSettings(currency: BlockChainCurrency, settings: WalletSettings): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, SetWalletSettings(settings))).mapTo[WalletStatus].map(CoinatorDataResponse[WalletStatus])

  def estimateTransaction(requestID: String, transaction: Transaction): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(transaction.amount.currency, EstimateTransaction(transaction.withRequestId(requestID))))
      .mapTo[Transaction].map(CoinatorDataResponse[Transaction])

  def getTransactionFeed(currency: BlockChainCurrency, parameters: TxFeedQuery): Future[CoinatorApiResponse] =
    walletCoordinator.ask(WalletMessage(currency, GetTransactionFeed(parameters))).mapTo[CoinatorApiResponse]
}