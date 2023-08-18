fu! JexadScript_ft()
  " TODO: escape sequences could be nice
  syn match Special /[()]/
  syn match Identifier /[a-z_][0-9a-z_]*/
  syn match Function /[A-Z][0-9A-Za-z]*/
  syn match PreProc /:[0-9A-Za-z_]\+/
  syn region String start=/"/ skip=/\\"/ end=/"/
  syn match Number /-\?\(0b[01]\+\|0o\o\+\|0x\x\+\|\d\+\(\.\d\+\)\?\|'.'\)/
  syn match Statement /([=$]/ contains=Special
  syn match Comment /#.*/
  setl cms=#%s com=:#
endf

au BufRead,BufNewFile *.jxds se ft=jxds
au FileType jxds cal JexadScript_ft()
