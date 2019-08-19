package cn.edu.gzmu.authorization.common.exception

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/19 上午11:06
 */
class BadRequestException : Exception{
  constructor() : super("请求参数不合法")
  constructor(msg: String) : super(msg)
  constructor(cause: Throwable) : super(cause)
}
