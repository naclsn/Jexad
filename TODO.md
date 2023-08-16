## notes on things not done yet in no particular order

- [~] no var-arg constructor, use lists/tuples (but keep overloading)
- [~] .(somewhat with above): the `arguments` method
- [~] finish cleanup with `Lookup.ClassesUnder`
- [~] build a list of funs (eg. build step)
    - [ ] make the classes from template (in build step)
- [~] make the view be `Obj`s
- [ ] error handling
- [ ] loading obj from external jars
- [?] Tostr, Tonum, Eval
- [ ] `(= )` and `($ )` syntaxes
- [~] outdate() things
- [ ] find uses of `Lst.at`/`Lst.length`, remove when possible (one less virtual fn call cant do bad..)
- [ ] symbol to enum

- [ ] caseB:
    - [ ] png decode, functions on the returned handle (what should an image look like? dohas a ..Pixel(x, y) or something?)
          should it try to be 'single set of fn for every img type'? eg. not PngGetPixel, but ImgGetPixel?
