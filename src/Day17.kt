import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.math.pow

fun main() = runBlocking(Dispatchers.Default) {
    class Puter {
        val input = readInput(day = 17, test = false)
        var regA = 0L
        var regB = 0L
        var regC = 0L
        var ins = 0
        val mem = mutableListOf<Long>()
        val out = mutableListOf<Long>()

        fun reset() {
            mem.clear()
            out.clear()
            ins = 0

            regA = input[0].substringAfter(": ").toLong()
            regB = input[1].substringAfter(": ").toLong()
            regC = input[2].substringAfter(": ").toLong()
            input[4]
                .substringAfter(": ")
                .split(',')
                .map(String::toLong)
                .also(mem::addAll)
        }

        init {
            reset()
        }

        fun printState() {
            println("RegA: $regA")
            println("RegB: $regB")
            println("RegC: $regC")
            println("Ins: $ins")
            println("Mem: ${mem.joinToString(",")}")
            println("Out: ${out.joinToString(",")}")
        }

        fun readLiteralOperand(): Long =
            mem[ins + 1]

        fun readComboOperand(): Long =
            when (val op = readLiteralOperand()) {
                in (0L..3L) -> op
                4L -> regA
                5L -> regB
                6L -> regC
                else -> error("Invalid combo operand $op")

            }

        val instructions = listOf(
            fun() {
                /*
                The adv instruction (opcode 0) performs division.
                 The numerator is the value in the A register.
                 The denominator is found by raising 2 to the power of the instruction's combo operand.
                 (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
                 The result of the division operation is truncated to an integer and then written to the A register.
                 */
                val op = readComboOperand()
//            println("adv $op")
                regA /= 2.0.pow(op.toDouble()).toLong()
            },
            fun() {
                /*
                The bxl instruction (opcode 1) calculates the bitwise XOR of register B
                and the instruction's literal operand, then stores the result in register B.
                 */
                val op = readLiteralOperand()
//            println("bxl $op")
                regB = regB xor op
            },
            fun() {
                /*
                The bst instruction (opcode 2) calculates the value of its combo operand modulo 8
                (thereby keeping only its lowest 3 bits), then writes that value to the B register.
                 */
                val op = readComboOperand()
//            println("bst $op")
                regB = op % 8 // can it be op and 0b111?
            },
            fun() {
                /*
                The jnz instruction (opcode 3) does nothing if the A register is 0.
                However, if the A register is not zero, it jumps by setting the instruction pointer
                to the value of its literal operand;
                if this instruction jumps, the instruction pointer is not increased by 2 after this instruction.
                 */
                if (regA == 0L) {
//                println("jnz -")
                    return
                }
                val op = readLiteralOperand()
//            println("jnz $op")
                ins = op.toInt()
            },
            fun() {
                /*
                The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C,
                then stores the result in register B.
                (For legacy reasons, this instruction reads an operand but ignores it.)
                 */
                readLiteralOperand()
//            println("bxc")
                regB = regB xor regC
            },
            fun() {
                /*
                The out instruction (opcode 5) calculates the value of its combo operand modulo 8,
                then outputs that value. (If a program outputs multiple values, they are separated by commas.)
                 */
                val op = readComboOperand()
//            println("out $op")
                out += op % 8
            },
            fun() {
                /*
                The bdv instruction (opcode 6) works exactly like the adv instruction
                except that the result is stored in the B register.
                (The numerator is still read from the A register.)
                 */
                val op = readComboOperand()
//            println("bdv $op")
                regB = regA / 2.0.pow(op.toDouble()).toLong()
            },
            fun() {
                /*
                The cdv instruction (opcode 7) works exactly like the adv instruction
                except that the result is stored in the C register.
                (The numerator is still read from the A register.)
                 */
                val op = readComboOperand()
//            println("cdv $op")
                regC = regA / 2.0.pow(op.toDouble()).toLong()
            }
        )

        fun run() {
            while (ins in mem.indices) {
                val eIns = ins
                instructions[mem[ins].toInt()].invoke()
                if (ins == eIns) {
                    ins += 2
                }
            }
        }
    }

    val puter = Puter()
    puter.run()
    puter.printState()
}
