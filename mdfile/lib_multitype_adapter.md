
[MultiTypeAdapter地址链接](https://github.com/drakeet/MultiType)

重要类的解析:

### MultiTypeAdapter : RecyclerView.Adapter

主要将adapter方法全部抽出一个代理方法类实现绑定，使用typePool保存所有的modleClass，多binders，指定具体binder位置类，将adapter的所有方法，调用代理类方法手动管理

包含typepool 和 数据items的属性; 支持通过typePool register 相应的viewholder信息;

几个重要重写方法的实现:

- register(class,itemviewbinder,link)

注册操作,此方法会事先会事先解绑操作,所以当一对多的情况下,会把之前的进行解绑; 

- register(modelclass) : onetoManyFLow<T>

提供流式操作的注册,也会把之前的解绑,返回一个OneToManyBuilder对象; OneToManyBuilder提供流式方法建立itemviewbinder数组和linker的联系; 最终也是通过不覆盖class的register方法(default修饰)注册; 构建单个modelClass : 多个itemviewbinder 的对应;  注意注册的modelclass不能同名,同名的会解绑后再注册;

- getItemViewType 

使用数据items的modelclass,在MultiTypePool容器中查找class的第一个index后(为了更好的定位一对多的情况),在使用对应index位置的Link处理ModelClass一对多的情况,获取到同种model多种itemviewbinder中的位置(即同Model的itemviewbinder的选择)再加上index 即为真正的itemType; 

eg:

amodel.class -> itemviewBinder1  - itemtype (0+0)
b.class -> itemviewBinder2 - itemtype(1+0)
b.class -> itemviewBinder3 - itemtype(1+1)
c.class -> itemViewBinder4 - itemtype(3+0)

可通过onetoManyFLow注册,to方法关联3种itemviewBinder,with方法通过Linker的index()返回,根据model 得到int,在加上 modelclass的位置得到不同的itemtype;

- onCreateViewHolder,onBindViewHolder

主要使用typePool通过itemtype找到对应的itemviewBinder,调用对应的代理方法执行构建view的操作;

- onViewAttachedToWindow,onViewDetachedFromWindow等

都是通过holder找到itemtype,然后typepool使用itemtype找到itemviewBInder,将这些方法都代理出去;


### MultiTypePool : TypePool

**TypePool**

作为item viewholder的特性抽象,是一个容器,内部包含 
注册/解绑viewholder的class,itemviewbinder,linker到容器中, 通过key获取存储的内容;

提供使用modelClass查找处于容器中的index,如果是modelClass 是 注册class的子类也可以命中;

**MultiTypePool**

内部包括以下三种类型的容器List,重写typePool的方法,用于向容器中添加/获取相应数据;

- Class<?> model类容器
- ItemViewBinder<?,?>  含model类, viewholder类,作为viewholder的代理 
- Linker<?> 用于 单模型-多绑定类 的位置查找；

### ItemViewBinder

作为adapter需要重写方法的抽象, 属于viewholder的代理,其中定义了ViewHolder的抽象方法和经常使用的方法;

添加数据泛型 + ViewHolder泛型；

保留typeadapter的引用 ， 建立 Adapter中常用方法的代理方法；

### Linker

接口;包含一个抽象方法 #index(int,T):Int, 表示 同一种ModelClass中存在一种数据类型对应多种布局时的选择,返回int加上modleClass的位置即为itemType; 

**ClassLinker** 

接口;用于对功能的扩展 #index(int,T):Class<ItemViewBinder> , 可以返回一个itemviewbinder中注册的class来代替直接的int,方便使用; 具体通过ClassLinkerWrapper适配;

### OneToManyBuilder : OneToManyFlow,OneToManyEndpoint

采用的是Rxjava那套构建方法, 非常好的一套设计式样  ！！！ 

用于register快速构建方式中 ,联系 linker,itemviewbinder 等参数; 

应该是属于(构建者)的变形; 

- 通过OneToManyFlow#to 加入itemviewbinders数组的引用到Builder中; 
- 通过OneToManyEndpoint#withXXX 添加对应的Linker
	- 如果添加的是ClassLinker, 重写接口index方法,返回给定的itemviewbinder位于itemviewbinder数组中的index; 得到不同的itemtype,实现一modelClass多itemviewbinder的对应关系;

**OneToManyFlow** 

接口,提供 抽象方法 to(Itemviewbinder[]):onetomanyEndpoint ;用于关联itemviewbinder数组;

**OneToManyEndpoint**

接口,提供withLinker(linker),withClassLinker(classlinker) ;用于关联linker 指定一对多情况下itemviewbinder的选择;

**ClassLinkerWrapper** 修饰了ClassLinker, 又实现了Linker的接口 ,将两个不兼容的类兼容起来(适配器模式)

实现Linker接口,本身是一个Linker; 作为ClassLinker的包装类(修饰者),提供wrap方法处理ClassLinker返回一个Linker(即自身),重写Linker的index方法,即完成了对Linker的扩展; 

index的实现为classLinker返回的itemviewBinder的class , 在itemviewBInder数组中查找index并返回;









