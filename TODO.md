## notes on things not done yet in no particular order

- [ ] Obj.update() throws Throwable (or other)
- [ ] Obj.at() throws ..
- [x] remove generic from Lst
- [x] raw types and other warnings

- [~] no var-arg constructor, use lists/tuples (but keep overloading)
- [~] .(somewhat with above): the `arguments` method
- [~] enum- arguments
- [~] finish cleanup with `Lookup.ClassesUnder`
- [~] build a list of funs (eg. build step)
    - [ ] make the classes from template (in build step)
- [x] make the view be `Obj`s (although ArgsTreeView was not ported)
- [ ] error handling
- [x] loading obj from external jars
- [?] Tostr, Tonum, Eval
- [~] `(= )` and `($ )` syntaxes
- [x] outdate() things
- [x] find uses of `Lst.at`/`Lst.length`, remove when possible (one less virtual fn call cant do bad..)
- [~] symbol to enum
- [ ] prefer symbol for handles

- [ ] just noticed but, is `At` correct? does it update correctly\*?

- [ ] caseB:
    - [ ] png decode, functions on the returned handle (what should an image look like? dohas a ..Pixel(x, y) or something?)
          should it try to be 'single set of fn for every img type'? eg. not PngGetPixel, but ImgGetPixel?
