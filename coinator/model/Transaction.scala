package service.model

import akka.http.scaladsl.model.StatusCodes
import io.circe._
import io.circe.syntax._
import scalaz.Memo
import service.model.currency.BlockChainCurrency
import service.rest.api.CoinatorDataResponse

case class Transaction(amount: Amount,
                       to: String,
                       from: Option[String],
                       confirmationLimit: Option[Int] = None,
                       transactionId: Option[String] = None,
                       status: Option[TransactionStatus] = None,
                       requestId: Option[String] = None,
                       comment: Option[String] = None,
                       fee: Option[Amount] = None,
                       feeRate: Option[Amount] = None,
                       initialBalance: Option[Amount] = None
                      ) {
  // HTTP 201	  Transaction has been created (200 for cached responses)
  def toDataResponse: CoinatorDataResponse[Transaction] = CoinatorDataResponse(this)(Transaction.encodeTransaction).withStatusCode(StatusCodes.Created)

  def withRequestId(newRequestId: String): Transaction = copy(requestId = Some(newRequestId))

  def withTransactionId(newTransactionId: String): Transaction = copy(transactionId = Some(newTransactionId))

  def withFee(newFee: Amount): Transaction = copy(fee = Some(newFee))

  def withFeeRate(newFeeRate: Option[Amount]): Transaction = copy(feeRate = newFeeRate)

  def withToAddress(newToAddress: String): Transaction = copy(to = newToAddress)

  def withStatus(newStatus: Option[TransactionStatus]): Transaction = copy(status = newStatus)

  def withStatus(newStatus: TransactionStatus): Transaction = copy(status = Some(newStatus))
}

object Transaction {

  import TransactionStatus._

  implicit def encodeTransaction: Encoder[Transaction] = Encoder.instance[Transaction] { t =>
    import t.amount.currency.amountFormat._
    Json.obj(
        "amount" -> t.amount.asJson,
        "currency" -> t.amount.currency.asJson,
        "to" -> t.to.asJson,
        "from" -> t.from.asJson,
        "confirmationLimit" -> t.confirmationLimit.asJson,
        "transactionId" -> t.transactionId.asJson,
        "status" -> t.status.asJson,
        "requestId" -> t.requestId.asJson,
        "comment" -> t.comment.asJson,
        "fee" -> t.fee.asJson,
        "feeRate" -> t.feeRate.asJson,
        "initialBalance" -> t.initialBalance.asJson
    )
  }

  implicit def decodeTransaction: BlockChainCurrency => Decoder[Transaction] =
    Memo.immutableHashMapMemo {
      currency: BlockChainCurrency =>
        Decoder.instance[Transaction] { c: HCursor =>
          for {
            amount <- c.downField("amount").as[Amount](currency.amountFormat.decoder)
            toAddress <- c.downField("to").as[String]
            fromAddress <- c.downField("from").as[Option[String]]
            confirmationLimit <- c.downField("confirmationLimit").as[Option[Int]]
            comment <- c.downField("comment").as[Option[String]]
            requestId <- c.downField("requestId").as[Option[String]]
            txId <- c.downField("transactionId").as[Option[String]]
            fee <- c.downField("fee").as[Option[Amount]](currency.amountFormat.decoderOpt)
            status <- c.downField("status").as[Option[TransactionStatus]]
            feeRate <- c.downField("feeRate").as[Option[Amount]](currency.amountFormat.decoderOpt)
            initialBalance <- c.downField("initialBalance").as[Option[Amount]](currency.amountFormat.decoderOpt)
          } yield Transaction(amount, toAddress, fromAddress, confirmationLimit, requestId = requestId,
            comment = comment, transactionId = txId, status = status, fee = fee, feeRate = feeRate,
            initialBalance = initialBalance
          )
        }
    }
}