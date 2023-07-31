#!/usr/bin/env python
import png

def readall(f):
    with open(f, 'rb') as f:
        return f.read()

img = list(map(list, zip(readall('r.txt'), readall('g.txt'), readall('b.txt'))))

with open('image.png', 'wb') as f:
    png.Writer(30, 30, greyscale=False).write(f, (sum(img[k*30:(k+1)*30], []) for k in range(30)))
