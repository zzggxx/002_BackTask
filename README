
# 创建后台的方法

## 一.使用 IntentService.

这是一个一直被人遗忘的Service，<font color=#ff0000>*但实际上却是Google一直推荐的后台任务工具类*</font>。IntentService是一个轻量级的Service，系统帮我们自动调用了Service的一些方法，让我们可以一键完成后台任务的建。

但IntentService与Service还是有所不同的：

1. IntentService运行在独立线程，可以直接执行耗时操作，不会阻塞UI线程
2. IntentService使用onHandleIntent来处理后台任务，处理完毕后就会自动退出，不用手动退出，并不会常住后台，想动歪脑筋的可以放弃了
3. IntentService的工作队列是单线程的，也就是说，每次只会操作一个IntentService，多个任务是排队处理的，新任务会等待旧任务的执行完成再执行，正在执行的任务和线程一样，是无法中断的(排队的一个队列)
4. IntentService本身是单向交互的，默认不存在回调UI线程的接口，这也是IntentService的一个局限，默认只能处理后台任务，但不能更新UI（但实际上可以）