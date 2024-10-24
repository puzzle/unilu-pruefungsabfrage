beforeEach(() => {
    cy.visit("/");
    cy.getByTestId("cookie-consent-button").click();
})

describe('Verify content of header', () => {
    it('should display the correct image', () => {
        cy.get("header").find("#unilu-logo-header")
            .should('have.attr', 'src')
            .and('include', 'UNILU_Schriftzug_Standard_schwarz_DE.svg');

    });

    it('should display navbar', () => {
        cy.get('header').then((header) => {
            expect(header).to.contain("Prüfungslaufnummer")
            expect(header).to.contain("Kontakt")
            expect(header).to.contain("Logout")
        })
    })

    //TODO: Write tests for student name once the login functionality is implemented
})

describe('Verify functionality of header', () => {
    it('should navigate to / when link to the search page is clicked', () => {
        const item = cy.get('header').contains('Prüfungslaufnummer')
        item.should('exist');
        item.click();
        cy.url().should('eq', Cypress.config().baseUrl + '/');
        cy.get('body').then((body) => {
            expect(body).to.contain("Suche nach einer Prüfung...")
        })
    });

    it('should navigate so /contact when link to the contact page is clicked', () => {
        const item = cy.get('header').contains('Kontakt')
        item.should('exist');
        item.click();
        cy.url().should('eq', Cypress.config().baseUrl + '/contact');
    });

    // TODO: Write test for 'Logout' once the functionality is implemented
})


