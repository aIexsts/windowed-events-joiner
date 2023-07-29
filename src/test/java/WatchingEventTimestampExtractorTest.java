import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.model.WatchingEvent;
import org.example.util.WatchingEventTimestampExtractor;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class WatchingEventTimestampExtractorTest {

	@Test
	void whenCorrectTimestampSupplied_thenParsedSuccessfully() {
		final var extractor = new WatchingEventTimestampExtractor();
		ConsumerRecord<Object, Object> consumedEvent = new ConsumerRecord<>(
				"start-watching-events",
				1,
				1L,
				"123",
				new WatchingEvent("123", 1, "2021-02-08 02:01:30")
		);
		long partitionTime = 456L;

		long time = extractor.extract(consumedEvent, partitionTime);

		assertThat(time).isGreaterThan(0);
		assertThat(time).isNotEqualTo(partitionTime);
	}

	@Test
	void whenInvalidTimestampSupplied_thenPartitionTimeReturned() {
		final var extractor = new WatchingEventTimestampExtractor();
		ConsumerRecord<Object, Object> consumedEvent = new ConsumerRecord<>(
				"start-watching-events",
				1,
				1L,
				"123",
				new WatchingEvent("123", 1, "2021-02-08 abcde")
		);
		long partitionTime = 456L;

		long time = extractor.extract(consumedEvent, partitionTime);

		assertThat(time).isEqualTo(partitionTime);
	}

	@Test
	void whenNullTimestampSupplied_thenPartitionTimeReturned() {
		final var extractor = new WatchingEventTimestampExtractor();
		ConsumerRecord<Object, Object> consumedEvent = new ConsumerRecord<>(
				"start-watching-events",
				1,
				1L,
				"123",
				new WatchingEvent("123", 1, null)
		);
		long partitionTime = 456L;

		long time = extractor.extract(consumedEvent, partitionTime);

		assertThat(time).isEqualTo(partitionTime);
	}
}
