package technology.mainthread.apps.isitup.data.vo;

public class NavigationDrawerItem {

    private final int iconResId;
    private final int titleResId;

    public int getIconResId() {
        return iconResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    private NavigationDrawerItem(Builder builder) {
        iconResId = builder.iconResId;
        titleResId = builder.titleResId;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static Builder Builder(NavigationDrawerItem copy) {
        Builder builder = new Builder();
        builder.iconResId = copy.iconResId;
        builder.titleResId = copy.titleResId;
        return builder;
    }


    public static final class Builder {
        private int iconResId;
        private int titleResId;

        private Builder() {
        }

        public Builder iconResId(int iconResId) {
            this.iconResId = iconResId;
            return this;
        }

        public Builder titleResId(int titleResId) {
            this.titleResId = titleResId;
            return this;
        }

        public NavigationDrawerItem build() {
            return new NavigationDrawerItem(this);
        }
    }
}
