package cn.gorouter.api;

public class RouterBinder implements IRouter {
    @Override
    public void add() {
        GoRouter.getInstance().add("url" , Page.class);
    }
}
