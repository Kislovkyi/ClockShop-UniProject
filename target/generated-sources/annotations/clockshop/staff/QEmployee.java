package clockshop.staff;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployee extends EntityPathBase<Employee> {

    private static final long serialVersionUID = -553827002L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmployee employee = new QEmployee("employee");

    public final org.salespointframework.core.QAbstractAggregateRoot _super = new org.salespointframework.core.QAbstractAggregateRoot(this);

    public final StringPath address = createString("address");

    public final StringPath email = createString("email");

    public final StringPath forename = createString("forename");

    public final NumberPath<Float> hourRate = createNumber("hourRate", Float.class);

    public final QEmployee_EmployeeIdentifier id;

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Integer> monthlyHours = createNumber("monthlyHours", Integer.class);

    public final StringPath name = createString("name");

    public final ComparablePath<java.util.UUID> passwordResetToken = createComparable("passwordResetToken", java.util.UUID.class);

    public final StringPath role = createString("role");

    public final NumberPath<Float> salary = createNumber("salary", Float.class);

    public final StringPath telephoneNumber = createString("telephoneNumber");

    public final org.salespointframework.useraccount.QUserAccount userAccount;

    public final StringPath username = createString("username");

    public QEmployee(String variable) {
        this(Employee.class, forVariable(variable), INITS);
    }

    public QEmployee(Path<? extends Employee> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmployee(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmployee(PathMetadata metadata, PathInits inits) {
        this(Employee.class, metadata, inits);
    }

    public QEmployee(Class<? extends Employee> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QEmployee_EmployeeIdentifier(forProperty("id")) : null;
        this.userAccount = inits.isInitialized("userAccount") ? new org.salespointframework.useraccount.QUserAccount(forProperty("userAccount"), inits.get("userAccount")) : null;
    }

}

