package cn.edu.gzmu.authorization.common.exception

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/21 下午5:40
 */
class TransactionException : Exception{
  constructor() : super("Transaction Exception")
  constructor(msg: String) : super(msg)
  constructor(cause: Throwable) : super(cause)
}
