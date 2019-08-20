package cn.edu.gzmu.authorization.common.exception

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/19 上午11:46
 */
class InternalServerErrorException  : Exception{
  constructor() : super("内部服务器错误")
  constructor(msg: String) : super(msg)
  constructor(cause: Throwable) : super(cause)
}
