package io.github.alexiscomete.lapinousecond.roles;

import java.util.function.Supplier;

public enum RolesEnum {
    ;

    private final Supplier<Role> supplier;

    public Role getInstance() {
        return supplier.get();
    }

    RolesEnum(Supplier<Role> supplier) {
        this.supplier = supplier;
    }
}
