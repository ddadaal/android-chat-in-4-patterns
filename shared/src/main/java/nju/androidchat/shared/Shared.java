package nju.androidchat.shared;


import lombok.Getter;

public class Shared {

    @Getter
    public int v;

    public void test() {
        System.out.println("Shared module called.");

        int i = this.getV();
    }
}
