# rtmp
```
$ file nginx
nginx: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/l, for GNU/Linux 2.6.32, BuildID[sha1]=9352653d09f7b5fb2a662b172b8d23414637b7c5, not stripped

$ ldd nginx
	linux-vdso.so.1 =>  (0x00007ffed0f3a000)
	libdl.so.2 => /lib/x86_64-linux-gnu/libdl.so.2 (0x00007fbbaf479000)
	libpthread.so.0 => /lib/x86_64-linux-gnu/libpthread.so.0 (0x00007fbbaf25c000)
	libcrypt.so.1 => /lib/x86_64-linux-gnu/libcrypt.so.1 (0x00007fbbaf024000)
	libpcre.so.3 => /lib/x86_64-linux-gnu/libpcre.so.3 (0x00007fbbaedb4000)
	libssl.so.1.0.0 => /lib/x86_64-linux-gnu/libssl.so.1.0.0 (0x00007fbbaeb4b000)
	libcrypto.so.1.0.0 => /lib/x86_64-linux-gnu/libcrypto.so.1.0.0 (0x00007fbbae706000)
	libz.so.1 => /lib/x86_64-linux-gnu/libz.so.1 (0x00007fbbae4ec000)
	libc.so.6 => /lib/x86_64-linux-gnu/libc.so.6 (0x00007fbbae122000)
	/lib64/ld-linux-x86-64.so.2 (0x00007fbbaf67d000)
```
