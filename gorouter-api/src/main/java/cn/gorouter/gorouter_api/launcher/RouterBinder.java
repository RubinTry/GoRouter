package cn.gorouter.gorouter_api.launcher;

public class RouterBinder implements IRouter {
    @Override
    public void add() {
        GoRouter.getInstance().add("url" , Page.class);
    }
}
