const EMAIL = "dan@gmail.com"
const PWD = "password"
const TITLE = "Dune" // book must exist & have stock

describe("Loan & return flow updates Available copies only", () => {
    beforeEach(() => {
        cy.loginViaApi(EMAIL, PWD)
    })

    /* helper: open book detail via search */
    const openDetails = () => cy.searchBookAndOpen(TITLE)

    /* numeric value after “Available:” (2nd number on the line) */
    const readAvailable = () =>
        cy
            .contains(/available:/i) // full line: "Copies: N | Available: M"
            .invoke("text")
            .then((t) => Number(t.match(/available:\s*(\d+)/i)![1]))

    it("Available decreases after loan and restores after return", () => {
        cy.intercept("GET", "/api/books/*").as("getBook")

        /* -------- initial state -------- */
        openDetails()
        cy.wait("@getBook")
        readAvailable().then((initialAvail) => {
            /* -------------- LOAN -------------- */
            cy.findByRole("button", { name: /^loan$/i }).click()
            cy.findByText(/book loaned/i).should("exist")
            cy.wait("@getBook")

            cy.contains(/available:/i).should(($el) => {
                const v = Number($el.text().match(/available:\s*(\d+)/i)![1])
                expect(v).to.eq(initialAvail - 1)
            })

            /* ------------- RETURN ------------- */
            cy.findByRole("link", { name: /^shelf$/i }).click()
            cy.findByRole("button", { name: /return book/i })
                .first()
                .click()
            cy.findByRole("button", { name: /^yes$/i }).click()
            cy.findByRole("dialog").should("not.exist")

            /* back to details */
            openDetails()
            cy.wait("@getBook")

            cy.contains(/available:/i).should(($el) => {
                const v = Number($el.text().match(/available:\s*(\d+)/i)![1])
                expect(v).to.eq(initialAvail)
            })
        })
    })
})