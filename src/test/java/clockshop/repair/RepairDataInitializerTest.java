package clockshop.repair;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.salespointframework.time.BusinessTime;
import org.springframework.data.util.Streamable;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link RepairDataInitializer}
 */
@ExtendWith(MockitoExtension.class)
class RepairDataInitializerTest {
    @Mock
    private RepairManagement repairManagement;

    @Mock
    private BusinessTime businessTime;

    @InjectMocks
    private RepairDataInitializer repairDataInitializer;

    @Disabled
    @Test
    void initialize_NoRepairs() {

        when(repairManagement.findAll()).thenReturn(Streamable.empty());

        repairDataInitializer.initialize();

        verify(repairManagement, atLeastOnce()).addRepair(any(Repair.class));
    }

    @Test
    void initialize_RepairsExist() {

        when(repairManagement.findAll()).thenReturn(Streamable.of(new Repair()));

        repairDataInitializer.initialize();

        verify(repairManagement, never()).addRepair(any(Repair.class));
    }
}