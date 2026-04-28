package com.example.caixa_batch.jobs.interest;

import com.example.caixa_batch.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feature 3 - Partitioner: Divides accounts into ranges for parallel processing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountRangePartitioner implements Partitioner {

    private final AccountRepository accountRepository;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<Long> accountIds = accountRepository.findAllSavingsAccounts()
                .stream()
                .map(a -> a.getId())
                .sorted()
                .toList();

        Map<String, ExecutionContext> partitions = new HashMap<>();

        if (accountIds.isEmpty()) {
            ExecutionContext ctx = new ExecutionContext();
            ctx.putLong("minId", 0L);
            ctx.putLong("maxId", 0L);
            partitions.put("partition0", ctx);
            return partitions;
        }

        int partitionSize = Math.max(1, (int) Math.ceil((double) accountIds.size() / gridSize));

        for (int i = 0; i < gridSize; i++) {
            int fromIndex = i * partitionSize;
            if (fromIndex >= accountIds.size()) break;

            int toIndex = Math.min(fromIndex + partitionSize - 1, accountIds.size() - 1);

            ExecutionContext ctx = new ExecutionContext();
            ctx.putLong("minId", accountIds.get(fromIndex));
            ctx.putLong("maxId", accountIds.get(toIndex));

            String partitionName = "partition" + i;
            partitions.put(partitionName, ctx);
            log.info("Created {} with minId={} maxId={}", partitionName, accountIds.get(fromIndex), accountIds.get(toIndex));
        }

        return partitions;
    }
}
