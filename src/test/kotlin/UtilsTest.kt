import kotlin.test.Test

class UtilsTest {

    @Test
    fun `convert string list into 2d int array`() {
        val list = listOf(
            "a b c d e f g",
            "h i j k l m n",
            "o p q r s t u"
            "v w x y z . ."
        )
        val array = list.to2dIntArray()
        println(array)
    }
}