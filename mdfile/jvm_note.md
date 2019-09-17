#jvm

关键字:  
`HotSpot VM`,`两级即时编译器`, `编译器和解释器混合工作模式`,`模块化`,
`混合语言`,`多核并行``函数式编程`,

-----

##自动内存管理

jvm 运行时数据区 

- 线程私有
	- Jvm栈 (JVM Stack) 
		- 
	- 本地方法栈 (Native Method Stack)
	- 程序计数器 (Program counter Register)
		- 当前线程所执行的字节码的`行号指示器`
		- 线程执行java方法, 计数器记录的是正在执行的虚拟机字节码指令的地址; 线程执行native方法,计数器则为Undefined,
		- 是唯一一个在jvm中没有规定任何OOM情况的区域;


- 线程共享
	- 堆 (Heap)
	- 方法区 (Method Area)

