package data

import javax.persistence.*

@Entity
@Table(name = "user", schema = "dev", catalog = "")
open class UserEntity {
    @get:Id
    @get:Column(name = "uid", nullable = false)
    var uid: Int? = null

    @get:Basic
    @get:Column(name = "nickname", nullable = false)
    var nickname: String? = null

    @get:Basic
    @get:Column(name = "email", nullable = false)
    var email: String? = null

    @get:Basic
    @get:Column(name = "password", nullable = false)
    var password: String? = null


    override fun toString(): String =
            "Entity of type: ${javaClass.name} ( " +
                    "uid = $uid " +
                    "nickname = $nickname " +
                    "email = $email " +
                    "password = $password " +
                    ")"

    // constant value returned to avoid entity inequality to itself before and after it's update/merge
    override fun hashCode(): Int = 42

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserEntity

        if (uid != other.uid) return false
        if (nickname != other.nickname) return false
        if (email != other.email) return false
        if (password != other.password) return false

        return true
    }

}

