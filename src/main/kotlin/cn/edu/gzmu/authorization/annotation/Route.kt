package cn.edu.gzmu.authorization.annotation

/**
 * Route
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午5:05
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Route(val value: String)
