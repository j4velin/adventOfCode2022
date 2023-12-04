import kotlin.test.Test
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun `convert string list into 2d char array with ignore`() {
        val list = listOf(
            "a b c d e f g",
            "h i j k l m n",
            "o p q r s t u",
            "v w x y z + -"
        )
        val array = list.to2dCharArray(ignore = ' ')

        assertEquals(array[0][0], 'a')
        assertEquals(array[1][0], 'b')
        assertEquals(array[3][2], 'r')
        assertEquals(array[6][3], '-')
    }
}