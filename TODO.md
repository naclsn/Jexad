## notes on things not done yet in no particular order

- [~] no var-arg constructor, use lists/tuples (but keep overloading)
- [~] .(somewhat with above): the `arguments` method
- [~] finish cleanup with `Lookup.ClassesUnder`
- [x] Num based on other than i32
- [x] finish fixing parser (bsilhfd, escapes, processExpr)
- [x] 'help' for a function (with ret type and all)
- [~] build a list of funs (eg. build step)
    - [ ] make the classes from template (in build step)
- [ ] error handling
- [ ] loading obj from external jars
- [ ] Tostr, Tonum, Eval
- [ ] `(= )` and `($ )` syntaxes

- [ ] caseB:
    - [ ] png decode, functions on the returned handle (what should an image look like? dohas a ..Pixel(x, y) or something?)
          should it try to be 'single set of fn for every img type'? eg. not PngGetPixel, but ImgGetPixel?
