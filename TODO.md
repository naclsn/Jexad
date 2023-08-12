## notes on things not done yet in no particular order

- [x] pass to comment far-braces, use import *
- [x] remove these 'notest', `ant test` output is enough
- [x] list: fully update in update, remove need to update each entry in use
- [x] lambda -> `Bind`
- [x] symbols
- [x] map
- [x] the views are not even fun yet
- [~] no var-arg constructor, use lists/tuples (but keep overloading..? -- see about using the annotation)
- [~] .(somewhat with above): the `arguments` method
- [~] finish cleanup with `Lookup.ClassesUnder`
- [ ] Num based on other than i32
- [ ] finish fixing parser (bsilhfd, escapes, processExpr)
- [x] Parse should be to parse a str to num, bytes to num should be a Decode/Encode probably
- [x] double buffering
- [/] annotation to make fun of a class
- [ ] 'help' for a function (with ret type and all)
- [~] build a list of funs (eg. build step.. using the annotation..?)
- [ ] error handling (esp. Lang)
- [ ] loading obj from external jars
- [ ] Tostr, Tonum, Eval

- [ ] caseB:
    - [ ] png decode, functions on the returned handle (what should an image look like? dohas a ..Pixel(x, y) or something?)
          should it try to be 'single set of fn for every img type'? eg. not PngGetPixel, but ImgGetPixel?
