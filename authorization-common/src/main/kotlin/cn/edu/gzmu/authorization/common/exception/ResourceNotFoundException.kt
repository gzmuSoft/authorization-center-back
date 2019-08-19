package cn.edu.gzmu.authorization.common.exception

/**
 *
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/19 上午10:54
 */
class ResourceNotFoundException : Exception{
  constructor() : super("资源不存在")
  constructor(msg: String) : super(msg)
  constructor(cause: Throwable) : super(cause)
}
