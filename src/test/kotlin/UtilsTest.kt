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

    @Test
    fun `cut range from the beginning`() {
        val originalRange = 10L..100L
        val cut = 0L..20L
        val cuttedRange = originalRange.cut(cut)

        assertEquals(1, cuttedRange.count())
        assertEquals(21L..100L, cuttedRange.first())
    }

    @Test
    fun `cut range from the end`() {
        val originalRange = 10L..100L
        val cut = 100L..110L
        val cuttedRange = originalRange.cut(cut)

        assertEquals(1, cuttedRange.count())
        assertEquals(10L..99L, cuttedRange.first())
    }

    @Test
    fun `cut range from the middle`() {
        val originalRange = 10L..100L
        val cut = 50L..75L
        val cuttedRange = originalRange.cut(cut)

        assertEquals(2, cuttedRange.count())
        assertEquals(10L..49L, cuttedRange.first())
        assertEquals(76L..100L, cuttedRange.last())
    }
}