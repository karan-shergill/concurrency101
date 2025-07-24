package learning.a_thread_creation;

class Class002 extends Thread{
    // IMPORTANT to understand why we need to override the run()
    @Override
    public void run() {
        System.out.println("Class001 | Current thread is: " + Thread.currentThread().getName());
    }
}

public class b_LearnExtendThread {
    public static void main(String[] args) {
        System.out.println("LearnExtendThread | main | Current thread is: " + Thread.currentThread().getName());

        Class002 class002Obj = new Class002();
        class002Obj.start();

        System.out.println("LearnExtendThread | main | Current thread is: " + Thread.currentThread().getName());
    }
}
