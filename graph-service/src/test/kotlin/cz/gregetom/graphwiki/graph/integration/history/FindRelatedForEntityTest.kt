package cz.gregetom.graphwiki.graph.integration.history

import cz.gregetom.graphwiki.api.graph.model.HistoryTO
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.support.data.HistoryDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.HistoryApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FindRelatedForEntityTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var personDataSupport: PersonDataSupport
    @Autowired
    private lateinit var historyDataSupport: HistoryDataSupport

    @Test
    fun findRelatedHistoryForEntityTest() {
        val person = personDataSupport.randomPerson()
        val firstRecord = historyDataSupport.randomHistory { it.copy(entityId = person.id) }
        val secondRecord = historyDataSupport.randomHistory { it.copy(entityId = person.id) }

        val history = httpGet.doGet(Array<HistoryTO>::class, HistoryApiLinks.forEntity(person.id).toUri()).toList()

        assertThat(history).asList().hasSize(2)
        assertThat(history.toTypedArray()).isSortedAccordingTo(
                Comparator<HistoryTO> { o1, o2 -> o1.created.compareTo(o2.created) }.reversed()
        )
        assertThat(history.first { it.created == firstRecord.created }).isEqualToComparingOnlyGivenFields(firstRecord,
                "id", "created", "type", "previousState", "currentState"
        )
        assertThat(history.first { it.created == secondRecord.created }).isEqualToComparingOnlyGivenFields(secondRecord,
                "id", "created", "type", "previousState", "currentState"
        )
    }
}
