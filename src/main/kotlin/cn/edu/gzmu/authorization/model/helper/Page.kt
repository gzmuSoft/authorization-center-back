package cn.edu.gzmu.authorization.model.helper

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

/**
 * page
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2019/8/14 下午9:12
 */
data class Page(
  /**
   * 总元素数
   */
  val totalElements: Int,
  /**
   * 当前页数
   */
  val number: Int,
  /**
   * 页大小
   */
  val size: Int,
  /**
   * 当前页元素个数
   */
  val numberOfElements: Int,
  /**
   * 内容
   */
  val content: JsonArray,
  /**
   * 总页数
   */
  val totalPages: Int = (totalElements + size - 1) / size
) {
  /**
   * 是否拥有内容
   */
  fun hasContent(): Boolean = !content.isEmpty

  /**
   * 第一页
   */
  fun isFirst(): Boolean = !hasPrevious()

  /**
   * 最后一页
   */
  fun isLast(): Boolean = !hasNext()

  /**
   * 是否有上一页
   */
  fun hasPrevious(): Boolean = number > 1

  /**
   * 是否有下一页
   */
  fun hasNext(): Boolean = number < totalPages

  fun toJson(): JsonObject {
    return JsonObject.mapFrom(this)
  }
}
