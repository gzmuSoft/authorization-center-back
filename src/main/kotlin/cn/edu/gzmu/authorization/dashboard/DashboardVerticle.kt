package cn.edu.gzmu.authorization.dashboard

import cn.edu.gzmu.authorization.annotation.Route
import cn.edu.gzmu.authorization.model.constant.DASHBOARD
import cn.edu.gzmu.authorization.web.ControllerVerticle

/**
 * Dashboard Verticle
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/13 下午5:18
 */
@Route(DASHBOARD) class DashboardVerticle : ControllerVerticle() {
  override suspend fun doGet(request: WebRequest): WebResponse {
    return super.doGet(request)
  }
}
