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
- `src/ops/`: operations that can be performed on object, eg. `Slice`
- `src/views/`: different presentations of objects, eg. `TextView`
- `test/`: you get the idea

### Basic Objects

- `Buf`: a buffer on raw bytes
- `Lst`: a list of `Buf` or `Num` (or `Lst`)
- `Num`: a number from bytes (eg. 4 bytes little-endian int)

### Operations

..

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
