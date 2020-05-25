## Glide 源码解析

链接: [github-glide](https://github.com/bumptech/glide)

官方简介: 

Glide是一个快速高效的Android图片加载库，注重于平滑的滚动。Glide提供了易用的API，高性能、可扩展的图片解码管道（decode pipeline），以及自动的资源池技术。

> 优点:

- 自动、智能地下采样(downsampling)和缓存(caching)，以最小化存储开销和解码次数；
- 积极的资源重用，例如字节数组和Bitmap，以最小化昂贵的垃圾回收和堆碎片影响；
- 深度的生命周期集成，以确保仅优先处理活跃的Fragment和Activity的请求，并有利于应用在必要时释放资源以避免在后台时被杀掉。

