beforeEach(() => {
    cy.visit("/");
    cy.get('[data-testid="cookie-consent-button"]').click();
})

describe('Header displays all elements', () => {
    it('should display the right image', () => {
        cy.get("header").find("#logo")
            .should('have.attr', 'src')
            .and('include', 'UNILU_Schriftzug_Standard_schwarz_DE.png');

    });

    it('should display navbar', () => {
        cy.get('header').then((header) => {
            expect(header).to.contain("Prüfungslaufnummer")
            expect(header).to.contain("Kontakt")
            expect(header).to.contain("Logout")
        })
    })
})

describe('Header provides expected functionality', () => {
    it('should navigate to /search when link to the Search Page is clicked', () => {
        const item = cy.get('header').contains('Prüfungslaufnummer')
        item.should('exist');
        item.click();
        cy.url().should('eq', Cypress.config().baseUrl + '/search');
        cy.get('body').then((body) => {
            expect(body).to.contain("Suche nach einer Prüfung...")
        })
    });

    // ToDo: Write tests for 'Kontakt' and 'Logout' once the functionality is implemented
})


