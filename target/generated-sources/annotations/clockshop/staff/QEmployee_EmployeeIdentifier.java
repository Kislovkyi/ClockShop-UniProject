package clockshop.staff;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEmployee_EmployeeIdentifier is a Querydsl query type for EmployeeIdentifier
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QEmployee_EmployeeIdentifier extends BeanPath<Employee.EmployeeIdentifier> {

    private static final long serialVersionUID = -1672219713L;

    public static final QEmployee_EmployeeIdentifier employeeIdentifier = new QEmployee_EmployeeIdentifier("employeeIdentifier");

    public final ComparablePath<java.util.UUID> identifier = createComparable("identifier", java.util.UUID.class);

    public QEmployee_EmployeeIdentifier(String variable) {
        super(Employee.EmployeeIdentifier.class, forVariable(variable));
    }

    public QEmployee_EmployeeIdentifier(Path<Employee.EmployeeIdentifier> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmployee_EmployeeIdentifier(PathMetadata metadata) {
        super(Employee.EmployeeIdentifier.class, metadata);
    }

}

