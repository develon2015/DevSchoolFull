package data

import javax.persistence.*

@Entity
@Table(name = "assets", schema = "dev", catalog = "")
open class AssetsEntity {
    @get:Id
    @get:Column(name = "index", nullable = false)
    var index: Int? = null

    @get:Basic
    @get:Column(name = "uid", nullable = false)
    var uid: Int? = null

    @get:Basic
    @get:Column(name = "path", nullable = false)
    var path: String? = null

    @get:Basic
    @get:Column(name = "filename", nullable = false)
    var filename: String? = null

    @get:Basic
    @get:Column(name = "name", nullable = false)
    var name: String? = null

    @get:Basic
    @get:Column(name = "ext", nullable = true)
    var ext: String? = null

    @get:Basic
    @get:Column(name = "type", nullable = true)
    var type: String? = null

    @get:Basic
    @get:Column(name = "upload_time", nullable = false)
    var uploadTime: java.sql.Timestamp? = null

    @get:Basic
    @get:Column(name = "size", nullable = false)
    var size: Long? = null

    @get:Basic
    @get:Column(name = "status", nullable = false)
    var status: Int? = null


    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "index = $index " +
                    "uid = $uid " +
                    "path = $path " +
                    "filename = $filename " +
                    "name = $name " +
                    "ext = $ext " +
                    "type = $type " +
                    "uploadTime = $uploadTime " +
                    "size = $size " +
                    "status = $status " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AssetsEntity

        if (index != other.index) return false
        if (uid != other.uid) return false
        if (path != other.path) return false
        if (filename != other.filename) return false
        if (name != other.name) return false
        if (ext != other.ext) return false
        if (type != other.type) return false
        if (uploadTime != other.uploadTime) return false
        if (size != other.size) return false
        if (status != other.status) return false

        return true
    }

}

