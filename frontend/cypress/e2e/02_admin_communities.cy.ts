const ADMIN_EMAIL = "admin2@gmail.com";
const ADMIN_PASS = "password";

describe("Admin Communities page", () => {
    beforeEach(() => {
        cy.loginViaApi(ADMIN_EMAIL, ADMIN_PASS)
    })

    it("lists communities and opens the selected one", () => {
        cy.visit("/admin/communities")

        /* at least one community card rendered */
        cy.get('a[href^="/communities/"]').should("have.length.greaterThan", 0)

        cy.get('a[href^="/communities/"]')
            .first()
            .then(($link) => {
                const href = $link.attr("href")!

                /* text of the FIRST typography inside the card = community name */
                const communityName = $link.find("span.MuiTypography-root, h6, h5, h4, p").first().text().trim()

                cy.wrap($link).click()

                cy.url().should("include", href)

                /* Heading on the community page contains that name */
                cy.findByRole("heading", {
                    name: new RegExp(`^${communityName}$`, "i"),
                }).should("be.visible")
            })
    })
})