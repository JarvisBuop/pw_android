# Flutter Document Core

dart 语言： 强类型语言，但是可以推断类型；

### 语言特征

> 修饰符

dart 没有修饰符，可以使用`_`开头代表library_private；表示仅仅在library中式可见的；

每一个dart app 是一个 library，即使它不使用库指令；

dart 拥有表达式和声明式两种；


> import

`as` 作为 library prefix；
`show/hide` 导入部分library；

- 懒加载library：

	- `deferred as `'name';
	- 需要library时，调用`await dartname.loadLibrary()`导入库文件， await 异步中load；


> 变量

var 声明, json表示对象；

dynamic 表示 Object；

final/const 表示常量；

属性支持只提供get/set方法；

```

	real-only non-final property;

	int get launchYear => 
		launchDate?.year;

```

> 构造器

类似于java，可以支持构造器命名在通过 ： 指向默认构造器（此处又类似于kotlin）；

ClassName or ClassName.identifier; 

名称构造器，重定向构造器，常量构造器，工厂构造器；

工厂构造器 factory关键字，不会总是返回一个新的实例对象，有可能从缓存中拿，有可能返回一个子类实例；简单理解就是支持final数据为空的构造方法，灵活的自定义构造方法；

> 控制流

for(var l in list)

for(int i=0;i<10;i++)

while(i<100)

> 方法 Function类型

样式类似于java方法， 简写语法使用`=>`代替`->`;

每个方法是对象，参数化方法，即每个方法就是一个属性，可以被指定为变量或者作为参数传递给其他的方法；

`=> expr` 表达式 是` {return expr;} `的简写； => 有时是一个箭头语法；

参数可以使用类似于kotlin `=` 设置参数的默认值；

匿名方法： 

```
	//,分隔，类型参数，类型可省略

	([[Type] param1[, ...]]){
		codeBlock;
	}

```

`词法范围`：

dart 是词法范围的语言，意味着变量的范围仅仅由代码布局静态决定的；

`词法闭包`: 

闭包 是一个方法对象，可以访问在词法范围内的变量，即使方法在原始范围之外调用；

dart中方法可以记录临时变量；（震惊 ！）

```
	//返回值是一个方法属性，(int)->int+input;
	Function makeAdder(int addBy) {
	  return (int i) => addBy + i;
	}

	void main(){
		// 分别记录两个变量，返回一个方法对象；
		var fun1 = markAdder(2);
		fun1(3);//2+3;
	}

```

> 导包

import 'dart:math' 导入核心库

import 'package:test/test.dart' 导入外部package的库

import 'path/to/my_other_file.dart' 导入文件；

> 继承

声明类似于java ，构造器类似于kotlin， extends 关键字， 

> 混合扩展

 `with` 添加一个混合的能力到指定类中，主要是用于在多个类的层级中重用类的代码；
 声明了构造器的类不能混合,继承于class的类不能混合，`只能混合Object`；

 除非作为普通类的混合，一般会使用`mixin`代替`class`声明类关键字；

 `on` 需要限制使用mixin的类型，mixin想要依赖调用mixin没定义的方法；可使用on限制mixin类的使用，表示仅仅继承或者实现mixin类的类才能使用使用on标记的类；

```

此处SingerDancer类可以融合MusicalPerformer类，必须继承或者with Musician才可使用；

class Musician {
// ...
}
mixin MusicalPerformer on Musician {
// ...
}
class SingerDancer extends Musician with MusicalPerformer {
// ...
}

```

> 接口和抽象类

implements ，abstract


> Exception throw

try{} on xxxException catch(e,s){} finally{}

使用`rethrow`重新抛出catch的异常；

> 隐式接口

每个类都隐式的定义了一个包含所有类和实现接口的实例成员的接口；

也就是说每个class 都可以被其他类实现,不过所有的实例方法和非fianl属性需要被重写；个人理解就是将interface去除，每个class实际上也是interface，被实现的class属性都是final的，否则需要重写，方法都是抽象的，也是需要重写；

单继承，多实现，多混合；

> 泛型

- 适当的指定泛型会导致更好的生成代码；
- 使用泛型减少代码重复；

`extends` 限制上限；

> Async 异步支持

异步function： 返回Future or Stream；
 使用`async` `await`关键字支持异步编程，以同步代码方式写异步代码；

 async 标记在方法体前，表示是异步的方法；
await  获取异步表达式的完全数据；仅仅在async方法中使用；


```

Future checkVersion() async {
  var version = await lookUpVersion();
  // Do something with version
}

```

`await Expr` 表达式的值通常是一个Future，表示一个肯定会返回对象的promise；

处理流数据： 

- 使用async 和一个异步loop（await for）
- 使用流api；

await for 用于获取流的结果，不能使用在Ui事件流上，ui事件流是无止境的；

- 一直等待直到stream发送数据；
- 执行for loop 的方法体，将变量设置为发出的数据；
- 重复1，2， 直到stream关闭；

当实现异步forloop时，获得一个编译期间错误，需要确保await for在一个async的方法中；

```

Future main() async {
  // ...
  await for (varOrType identifier in expression) {
    handleRequest(identifier);
  }
  // ...
}

```

> 生成器方法

- 同步生成器： 返回 Iterable 对象；
	- 方法体后使用`sync*`标记，且使用yield 声明发送的数据；
- 异步生成器：返回Stream 对象；
	- 方法体后使用`async*`标记，使用yield声明发送的数据；

如果生成器是递归的，可以使用`yield*`提高性能；

```

Iterable<int> naturalsTo(int n) sync* {
  int k = 0;
  while (k < n) yield k++;
}

Stream<int> asynchronousNaturalsTo(int n) async* {
  int k = 0;
  while (k < n) yield k++;
}

Iterable<int> naturalsDownFrom(int n) sync* {
  if (n > 0) {
    yield n;
    yield* naturalsDownFrom(n - 1);
  }
}

```

> typedefs

dart中，方法即对象，和基本属性一样都是对象；

typedef，方法类型别名 可以直接声明字段和返回类型；

typedef可以在方法类型被指定为变量时保留类型的信息；


```

int add<T>(T a,T b) =>0;

typedef Add<T> = int Function(T a,T b);

```

> 元数据

使用元数据添加信息到代码上；

元数据注解 @deprecated ，@override；

自定义注解： 可以在运行时使用反射检索元数据；

```

library todo;

class Todo{
	final String who;
	final String what;
	const Todo(this....);
}

import 'todo.dart'

@Todo('','')
void doSome(){

}

```

> 协变&逆变

pe-cs： product-extends,consumer-super; 

List<String> = List<? extend Object>

get生产者方法安全，add消费者方法不安全；

此种情况下，整体为生产者，只能get 不能add； 记为product-extends；

List<Object> = List<? super String>

get生产者方法不安全，add消费者方法安全；

此种情况下，整体为消费者，只能add 不能get； 记为consumer-super；

dart中 当重写一个方法时，生产者和消费者的规则已经可以应用了；

![](https://dart.dev/guides/language/images/consumer-producer-methods.png)

对消费者来说，如`void chase(Animal a){}`，可以使用superType替代参数类型；对于生产者来说，如`Animal get parent =>`，可以使用subType替代返回值类型；

### effective

支持三元表达符；

ervis 表达式判空； 使用`??`表示，kotlin使用`?:`表示； if null；

b??=value; //表示b！=null 赋值给b，否则保持原状；

`<Point>[]` 表示list
`<String,Address>{}` 表示map
`<int>{}` 表示set

`List.from()`可更改泛型； var ints = List<int>.from(numbers);

`where`表达式 + `whereType()`过滤类型；

`=>`方法表达式，也可定义一个成员；

`const`表示常量；

`typedef` 可定义只有一个方法的callback类； typedef Predicate<E> = bool Function(E element);

`小瀑布符号 cascade notation`：dart语法，流式调用使用`..`代替；表示在同一对象上构建操作符的顺序调用；且支持构建临时变量可以写更多的流式code；

`...<?>` ...list  表示伸展符；

`@required` 表示参数是强制的；

runtimeType 表示对象的类型；

### 隔离 isolates


### effective rule

dart widget 倾向于组合，而不是继承；

组件间的通信 通过 `Navigator`和`Route` ，所有的都在同一个Activity中；
Route 是 屏幕 或者 页面 的抽象概念，Navigator是一个管理Route的widget；

Flutter中导航：

- 指定一个route names的Map（使用MaterialApp）
- 直接导航到route （使用WidgetsApp）

### 常用widget

runApp 接受给定的widget 作为widget的根布局；分为`StateLessWidget`， `StatefulWidget`，取决于是否要管理一些状态； widget主要实现是`build`函数构建自身；

- StateLessWidget： 无状态widget从父widget中接受参数，被储存在final型的成员变量中； 当一个widget被要求构建时，使用这些存储的值作为参数来构建widget；

- StatefulWidget： 特殊的widget，内部使用State保持状态； 这两种类型的对象具有不同的生命周期：widget是临时对象，用于构建当前状态下的应用程序，state对象在多次调用build（）之间保持不变，允许它们记住信息（状态）；

flutter中，事件流式向上传递的，状态流是向下传递的；

> Text 

格式文本

> Row,Column

水平，垂直 Flexbox布局；

> Stack

类似线性布局，Positioned 绝对定位；

> Container

页边距，内边距，约束；

### Canvas

> GestureDetector

> RenderBox (context.findRenderObject())

> CustomPainter




