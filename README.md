# Quasitype on Scala

Цель проекта -- описать спецификации посредством морфизмов и объектов,
как в теории категорий. Поскольку в данный момент рассматривается только Tagless Final реализация,
интересовать нас будет в основном то, что находится в директориях [corefinal](https://github.com/nekit2-002/quasitype/tree/master/modules/core/src/main/scala/com/tylip/quasitype/corefinal) и 
[scalacheckimpl](https://github.com/nekit2-002/quasitype/tree/master/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl).

## Цели и задачи некоторых абстрактных типов
Началом всего служит [Binding Algebra](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L6).
В ней определяются типы [UU](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L7) и [UFomula](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L8). Сокращение **UU** следует читать как **universal unit**.

**UFormula** и строящиеся на нем функции рассмотрим немного позже, а сначала проясним назначение **UU**.
Если посмотреть на наследника **BindingAlgebra** [LogicAlgebra](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L13), то можно увидеть, что внутри него есть [elem](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L21) и [eql](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L23), которые принимают абстрактное множество и два элемента типа **UU**. Если
посмотреть на реализацию [Set](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L23) и [eql](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L30), можно утверждать, что **Set** это некое абстрактное логическое
множество, для которого определены свойства того, является ли что-то его элементом и операция сравнения
элементов в контексте этого множества. Таким образом назначение **UU** таково, что оно используется
для реализации обобщения операции сравнения двух произвольных объектов в контексте какого-то множества.

Теперь что касается **UFormula**. Если посмотреть на её [реализацию](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L21), а также на реализацию [eqlF](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L33), можно увидеть что аналогичные им
объекты как бы абстрагированы на один уровень и принимаются на вход некий **List[Any]**. Этот **List[Any]**
имеет смысл контекста (но не того, о котором шла речь чуть ранее).
Чтобы понять, каков смысл контекста, рассмотрим объявление и реализацию функции [forall](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L26). **PFormula** -- это абстрагированный **Prop** (см [реализацию](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L17)).
С точки зрения семантики **PFormula** следует воспринимать как логический предикат. Реализован **forall**
вот [так](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L68). Как из
него видно, он генерирует множество (которое на деле во время выполнения оказывается множеством Hom), а затем для каждого его элемента выполняет предикат **j**, подставив в него контекст, дополненный этим
элементом (исходя из того, что генерируемое множество -- Hom, можно догадаться, что элемент это морфизм).

**forall** Реализован таким способом вот зачем. Если посмотреть уже на [алгебру категорий](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L53), а в частности на реализацию закона об [ассоциативности
композиции](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L68), можно увидеть, что в каждый
**forall** первым параметром передается **Hom** для двух последовательных объектов. Это значит, что
когда мы начинаем проверять свойство с помощью QuickCheck, у нас за одну итерацию на каждом шаге рекурсии
выбирается и кладется в контекст один морфизм, и таким образом, когда нам нужно запустить проверку равенства
морфизмов, в контексте находятся три морфизма, для которых мы это свойство проверяем. За счет рекурсии же создается некое подобие декартова произведения трех **Hom** множеств и таким образом эмулируется всеобщеность закона. Функция [pop](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L10) же нужна для того чтобы вытащить элемент из контекста
по определенном индексу.

Таким образом все функции с суффиксом **F** являются копиями себя с поправкой на то, что в них нужно дополнительно передать контекст, а **UFormula** необходима для корректировки параметров таким образом чтобы
учесть контекст.

## Зачем SpecSet?
**SpecSet** ([объявление тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L33)) нужен для того, чтобы при написании интерпретатора можно было запустить все тесты разом.
Если посмотреть на реализацию запуска тестов [вот тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L194) и [вот тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/testing/src/main/scala/com/tylip/quasitype/scalacheckimpl/CategAlgebraScalacheckImpl.scala#L201), то можно увидеть, что для прогонки всех тестов тестировщик итерируется по **allSpecs**.

## Некоторые вещи, которые даже после бесед с преподом я не понял.
По какой логике добавляются множества в **allSets** и  **allSetsNamed** [см тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L48). добавляются новые элементы. Единственные места, где я это увидел, это [тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L154) и [тут](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L317).

Остались еще некоторые вещи, но для зачета они не столь важны пока.

## Что маст хев для зачета
Для зачета необходимо на хаскель переписать **CategAlgebra** и написать для нее интерпретатор в тесты. Если получится, можно еще каких нибудь ее наследников по типу [CategGetSetAlgebra](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L253) или [CategNatAlgebra](https://github.com/nekit2-002/quasitype/blob/7d1769d0439c84bf3bf5634108e6872f0061f205/modules/core/src/main/scala/com/tylip/quasitype/corefinal/CategAlgebra.scala#L317) тоже сделать, чтоб в ПЗ все выглядело получше.

## Какие были мысли и какие проблемы возникли
**Догадка:** мне кажется ото всех функций с суффиксом F, отвечающих за поправку на контекст можно было бы
избавиться, передавая контекст неявно, а тип возвращаемого значения у всех функций завернуть
в монаду State. Но в таком случае нужно думать, как переписывать **forall**, чего я пока не знаю как сделать.

**Главная проблема:** переопределение типов. Я уже полностью забил на инкапсуляцию, которая так-то в скале есть, но даже так с помощью associated type family переписать такую функциональность не получается.

**Проблема номер 2:** чем должны быть параметризованы интерфейсы **CategAlgebra** и выше. На Scala
этих параметров нет, потому что все интерпретации делаются за счет наследования, но в хаскеле параметры типовые точно должны быть. Но какой они должны нести смысл -- вопрос на засыпку.

