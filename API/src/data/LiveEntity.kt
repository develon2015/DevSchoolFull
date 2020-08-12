package data

import javax.persistence.*

@Entity
@Table(name = "live", schema = "dev", catalog = "")
open class LiveEntity {
    @get:Id
    @get:Column(name = "index", nullable = false)
    var index: Int? = null

    @get:Basic
    @get:Column(name = "uid", nullable = false)
    var uid: Int? = null

    @get:Basic
    @get:Column(name = "md5", nullable = false)
    var md5: String? = null

    @get:Basic
    @get:Column(name = "title", nullable = false)
    var title: String? = null

    @get:Basic
    @get:Column(name = "category", nullable = false)
    var category: String? = null

    @get:Basic
    @get:Column(name = "status", nullable = false)
    var status: String? = null

    @get:Basic
    @get:Column(name = "order_time", nullable = false)
    var orderTime: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "live_time", nullable = false)
    var liveTime: java.sql.Timestamp? = null


    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "index = $index " +
                    "uid = $uid " +
                    "md5 = $md5 " +
                    "title = $title " +
                    "category = $category " +
                    "status = $status " +
                    "orderTime = $orderTime " +
                    "liveTime = $liveTime " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LiveEntity

        if (index != other.index) return false
        if (uid != other.uid) return false
        if (md5 != other.md5) return false
        if (title != other.title) return false
        if (category != other.category) return false
        if (status != other.status) return false
        if (orderTime != other.orderTime) return false
        if (liveTime != other.liveTime) return false

        return true
    }

}

