Goal is to have a framework/tooling to manipulate files at a byte (assumed
octet) level. For example:
- view a file in a traditional hex view
- knowing the offset and size of a static array of pointers to C-strings in a
  file, visualize said strings line wise in a text view
- extract by path the bytes of a PNG from a ZIP and visualize the
  red/green/blue components as 3 individual texts, editing the ZIP file updates
  the 3 texts
- ...

Project uses Apache ANT:
```console
$ ant run
...

$ ant jar  # to simply generate jar/Jexad.jar
```

## title

- `src/base/`: basic (`update()`-able) objects, eg. `Buf`
- `src/inter/`: interaction with the tool, eg. front-end script, list defined objects
- `src/ops/`: operations that can be performed on object, eg. `Slice`
- `src/views/`: different presentations of objects, eg. `TextView`
- `test/`: you get the idea

### Basic Objects

- `Buf`: a buffer on raw bytes
- `Lst`: a list of objects
- `Num`: a number, either integral or decimal (Java's `Big[..]`)
- `Fun`: a factory factory of doom
- `Sym`: an immutable symbol (eg. `:coucou`)

> - after updating a `Lst`, its elements are also up to date
> - updating `Fun` once before some `Fun.call`(s)
> - arguments to a `Fun.call` are not up to date
> - result from a `Fun.call` is not up to date
> - no updating `Sym`, it cannot depend on anything anyway

### Script Front-End

A tiny DSL is hacked together to define objects; this is the full syntax:
```plaintext
<script> ::= <var> '=' <expr> {';' <var> '=' <expr>} [';']
<expr> ::= <atom> | <fun> {<expr>} | <expr> ',' <expr>
<atom> ::= <str> | <num> | <lst> | <fun> | <sym> | <var> | '(' <expr> ')'

<comment> ::= '#' /.*/ '\n'

<str> ::= '"' /[^"]/ '"'
<num> ::= ['-'] /0x[0-9A-Fa-f_]+|0o[0-8_]+|0b[01_]+|[0-9_](\.[0-9_])?|'.'/
<lst> ::= '{' [<atom> {',' <atom>}] '}'
<fun> ::= /[A-Z][0-9A-Z]+/
<sym> ::= ':' /[0-9A-Za-z_]+/
<var> ::= /[a-z_][0-9a-z_]+/
```

Example:
```shell
filebuf = Read filename;
lst = Slice filebuf list_off list_end
    , Rect _ 4
    ; # hint: read the ',' as 'then'
ptrs = Map Parse lst;
```

Few random notes:
- the grammar above does not mention the escape sequences (the common ones, as well as `\x` `\u` `\U`)
- lists of mixed types are accepted, this is how tuples and var-args are 'supported'
- Java-side 'generics' are purely cosmetic

### Operations

Operations are Java classes that you can make `Fun` of.. uh...
(For example the class `Split` is the function `Split` in script.)

### Views on a Buffer

There are 3 view planned/implemented, all are on a byte buffer:
- a text view (your usual notepad)
- a hex view (like `xxd -g1`)
- an image view (1 byte palette, 3 bytes RGB/BGR bytes or 4 with alpha)

<!--
Once a view is used to edit its attached buffer, it gets "detached": a copy of
the buffer is made and edits are performed on this copy. This edit buffer it is
not accessible for further construction. In this state, the view no longer
updates with the buffer it was originally attached to, but it keeps a reference
to it. Restoring this reference ("re-attaching") will drop the edit buffer. The
edit buffer can be saved to a file.
-->
