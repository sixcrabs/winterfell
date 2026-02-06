package org.winterfell.misc.em;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
public class VinEventTest {

    public static void main(String[] args) throws InterruptedException {
        VinEventManager eventManager = new VinEventManager(MyEvent::new, new MyHandlerRegistry());
        //
        for (int i = 0; i < 10; i++) {
            eventManager.dispatch("hello world-".concat(String.valueOf(new Random().nextInt())));
        }

        System.out.println("-----main end-------");
        Thread.sleep(50000);
    }

    /**
     * 注册 事件消费者
     * spring 环境中一般是从 context 中获取 bean的实现
     */
    static class MyHandlerRegistry implements VinEventHandlerRegistry<MyEvent> {

        @Override
        public List<VinEventHandler<MyEvent>> eventHandlers() {
            List<VinEventHandler<MyEvent>> list = new ArrayList<>();
            list.add(new MyEventHandler());
            list.add(new YourEventHandler());
            return list;
        }
    }

    /**
     * 处理事件
     */
    static class MyEventHandler implements VinEventHandler<MyEvent> {

        @Override
        public void onEvent(MyEvent myEvent, long sequence, boolean endOfBatch) throws Exception {
            System.out.printf("from my handler: %s, sequence: %d, end: %s%n", myEvent.toString(), sequence, endOfBatch);

        }

        @Override
        public boolean support(Class<MyEvent> eventClass) {
            return eventClass.equals(MyEvent.class);
        }

        /**
         * 控制消费的次序 越小越在前
         *
         * @return
         */
        @Override
        public int ordinal() {
            return 100;
        }
    }

    static class YourEventHandler implements VinEventHandler<MyEvent> {

        @Override
        public boolean support(Class<MyEvent> eventClass) {
            return MyEvent.class.equals(eventClass);
        }

        /**
         * 控制消费的次序 越小越在前
         *
         * @return
         */
        @Override
        public int ordinal() {
            return 1;
        }

        @Override
        public void onEvent(MyEvent myEvent, long l, boolean b) throws Exception {
            System.out.println("your are handling.... waiting..");
            Thread.sleep(2000);
            System.out.println(String.format("from your handler: %s, sequence: %d, end: %s", myEvent.toString(), l, b));
        }
    }

    /**
     * 事件定义
     */
    static class MyEvent implements VinEvent<String> {

        private String message;

        public MyEvent() {
        }

        public String getMessage() {
            return message;
        }

        public MyEvent setMessage(String message) {
            this.message = message;
            return this;
        }

        @Override
        public void setPayload(String payload) {
            this.message = payload;

        }

        @Override
        public String toString() {
            return "MyEvent{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }

}