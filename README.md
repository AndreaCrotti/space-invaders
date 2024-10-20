![test and lint](https://github.com/AndreaCrotti/space-invaders/actions/workflows/test.yml/badge.svg)

# Space invaders

Run for example with babashka:

```sh
./space_script.bb -r resources/radar.txt -i resources/inv1.txt
```

You can change the fuzziness level with:

```sh
./space_script.bb -r resources/radar.txt -i resources/inv1.txt -f 0.6
```

Which means that any shape that matches at least 60% would be detected.

## Implementation

The implementation is as functional as possible, all the functions are simply manipulating data structures.

The arguments parsing is done in the [babashka script](./space_script.bb).

The main data structure is a vector of strings like:

```clojure
["ooo" "--o" "o-o"]
```

and we use that to represent both the radar content and the invaders.
To find the matching invaders we generate all the possible rectangles from the radar of the same shape of the radar, and compute the ratio of matching characters.

To handle edges we also go outside of the boundaries of the overall matrix.

This strategy is overall not very efficient, since every extra invader would cause a full re-analysis of the matrix.
However, for the purpose of this exercise I didn't try to make the solution efficient, and just focused on clean and simple code.
