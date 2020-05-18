package cz.gregetom.graphwiki.graph.integration.search

import cz.gregetom.graphwiki.api.graph.model.SearchCompanyRecord
import cz.gregetom.graphwiki.api.graph.model.SearchPersonRecord
import cz.gregetom.graphwiki.api.graph.model.SearchResult
import cz.gregetom.graphwiki.commons.test.RandomGenerator
import cz.gregetom.graphwiki.commons.test.TestUsers
import cz.gregetom.graphwiki.graph.AbstractIntegrationTest
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Company
import cz.gregetom.graphwiki.graph.dao.gremlin.data.vertex.Person
import cz.gregetom.graphwiki.graph.services.search.SearchService
import cz.gregetom.graphwiki.graph.support.data.CompanyDataSupport
import cz.gregetom.graphwiki.graph.support.data.PersonDataSupport
import cz.gregetom.graphwiki.graph.web.SearchApiLinks
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class FulltextSearchTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var companyDataSupport: CompanyDataSupport
    @Autowired
    private lateinit var personDataSupport: PersonDataSupport

    @Test
    fun fulltextSearchTest() {
        val query = RandomGenerator.randomString(5)
        val personGivenName = personDataSupport.randomActivePerson { it.copy(givenName = query) }
        val personFamilyName = personDataSupport.randomActivePerson { it.copy(familyName = query) }
        val company = companyDataSupport.randomActiveCompany { it.copy(officialName = query) }
        // inactiveCompany
        companyDataSupport.randomInactiveCompany { it.copy(officialName = query) }

        val userSearchResult = httpGet.doGet(
                SearchResult::class, SearchApiLinks.fulltextSearch(query, 1).toUri(), TestUsers.USER
        )

        assertThat(userSearchResult.persons.size).isEqualTo(2)
        assertThat(userSearchResult.companies.size).isEqualTo(1)
        checkPersonRecord(personFamilyName, userSearchResult.persons.find { it.familyName == personFamilyName.familyName })
        checkPersonRecord(personGivenName, userSearchResult.persons.find { it.familyName == personGivenName.familyName })
        checkCompanyRecord(company, userSearchResult.companies.first())


        val adminResult = httpGet.doGet(
                SearchResult::class, SearchApiLinks.fulltextSearch(query, 1).toUri(), TestUsers.ADMIN
        )
        assertThat(adminResult.persons.size).isEqualTo(2)
        assertThat(adminResult.companies.size).isEqualTo(2)
    }

    @Test
    fun fulltextSearchPageableTest() {
        val query = "query"
        val personsDataSize = (SearchService.PAGE_SIZE * 2.5).toLong()
        for (i in 1..personsDataSize) {
            personDataSupport.randomPerson { it.copy(givenName = query) }
        }
        val companiesDataSize = (SearchService.PAGE_SIZE * 2).toLong()
        for (i in 1..companiesDataSize) {
            companyDataSupport.randomCompany { it.copy(officialName = query) }
        }

        httpGet.doGet(SearchResult::class, SearchApiLinks.fulltextSearch(query, 1).toUri(), TestUsers.ADMIN).let {
            assertThat(it.anyDataAvailable).isTrue()
            assertThat(it.personsAvailableCount).isEqualTo(personsDataSize)
            assertThat(it.persons.size).isEqualTo(SearchService.PAGE_SIZE)
            assertThat(it.companiesAvailableCount).isEqualTo(companiesDataSize)
            assertThat(it.companies.size).isEqualTo(SearchService.PAGE_SIZE)
            assertThat(it.links.nextPage).isNotNull
        }

        httpGet.doGet(SearchResult::class, SearchApiLinks.fulltextSearch(query, 2).toUri(), TestUsers.ADMIN).let {
            assertThat(it.personsAvailableCount).isEqualTo(personsDataSize)
            assertThat(it.persons.size).isEqualTo(SearchService.PAGE_SIZE * 2)
            assertThat(it.companiesAvailableCount).isEqualTo(companiesDataSize)
            assertThat(it.companies.size).isEqualTo(companiesDataSize)
            assertThat(it.links.nextPage).isNotNull
        }

        httpGet.doGet(SearchResult::class, SearchApiLinks.fulltextSearch(query, 3).toUri(), TestUsers.ADMIN).let {
            assertThat(it.personsAvailableCount).isEqualTo(personsDataSize)
            assertThat(it.persons.size).isEqualTo(personsDataSize)
            assertThat(it.companiesAvailableCount).isEqualTo(companiesDataSize)
            assertThat(it.companies.size).isEqualTo(companiesDataSize)
            assertThat(it.links.nextPage).isNull()
        }
    }

    @Test
    fun fulltextSearchNonActiveVertexTest() {
        val query = RandomGenerator.randomString(10)
        // activePerson
        personDataSupport.randomActivePerson { it.copy(givenName = query) }
        // inactivePerson
        personDataSupport.randomInactivePerson { it.copy(givenName = query) }
        // activeCompany
        companyDataSupport.randomActiveCompany { it.copy(officialName = query) }
        // inactiveCompany
        companyDataSupport.randomInactiveCompany { it.copy(officialName = query) }

        val userSearchResult = httpGet.doGet(
                SearchResult::class, SearchApiLinks.fulltextSearch(query, 1).toUri(), TestUsers.USER
        )
        assertThat(userSearchResult.persons.size).isEqualTo(1)
        assertThat(userSearchResult.companies.size).isEqualTo(1)


        val adminSearchResult = httpGet.doGet(
                SearchResult::class, SearchApiLinks.fulltextSearch(query, 1).toUri(), TestUsers.ADMIN
        )
        assertThat(adminSearchResult.persons.size).isEqualTo(2)
        assertThat(adminSearchResult.companies.size).isEqualTo(2)
    }

    private fun checkPersonRecord(sourcePerson: Person, actual: SearchPersonRecord?) {
        require(actual != null)
        assertThat(actual.id).isEqualTo(sourcePerson.id)
        assertThat(actual.givenName).isEqualTo(sourcePerson.givenName)
        assertThat(actual.familyName).isEqualTo(sourcePerson.familyName)
        assertThat(actual.dateOfBirth).isEqualTo(sourcePerson.dateOfBirth)
        assertThat(actual.links.self.href).isNotNull()
        assertThat(actual.links.graph.href).isNotNull()
        assertThat(actual.links.vertex.href).isNotNull()
    }

    private fun checkCompanyRecord(sourceCompany: Company, actual: SearchCompanyRecord?) {
        require(actual != null)
        assertThat(actual.id).isEqualTo(sourceCompany.id)
        assertThat(actual.officialName).isEqualTo(sourceCompany.officialName)
        assertThat(actual.registrationNumber).isEqualTo(sourceCompany.registrationNumber)
        assertThat(actual.headquarters).isEqualToComparingFieldByField(sourceCompany.headquarters)
        assertThat(actual.links.self.href).isNotNull()
        assertThat(actual.links.graph.href).isNotNull()
        assertThat(actual.links.vertex.href).isNotNull()
    }
}
