#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import os


year = 2023

dir = 'aoc' + str(year)
os.makedirs(dir, exist_ok=True)

for i in range(1,26):
    if i < 10:
        day = 'Day0' + str(i)
    else:
        day = 'Day' + str(i)
    if os.path.exists(os.path.join(dir, day + '.txt')):
        continue
    open(os.path.join(dir, day + '.txt'), 'w').close()
    open(os.path.join(dir, day + '_test.txt'), 'w').close()
    with open(os.path.join(dir, day + '.kt'), 'w') as f:
        f.write('package ' + dir + '\n\n')
        f.write('import readInput\n\n')
        f.write('object ' + day + ' {\n')
        f.write('\tfun part1(input: List<String>): Int {\n\t\treturn 0\n\t}\n\n')
        f.write('\tfun part2(input: List<String>): Int {\n\t\treturn 0\n\t}\n}\n\n')
        f.write('fun main() {\n')
        f.write('\tval testInput = readInput("' + day + '_test", ' + str(year) + ')\n')
        f.write('\tcheck(' + day + '.part1(testInput) == 0)\n')
        f.write('\tcheck(' + day + '.part2(testInput) == 0)\n\n')
        f.write('\tval input = readInput("' + day + '", ' + str(year) + ')\n')
        f.write('\tprintln(' + day + '.part1(input))\n')
        f.write('\tprintln(' + day + '.part2(input))\n')
        f.write('}\n')
