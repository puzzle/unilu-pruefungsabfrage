beforeEach(() => {
    cy.visit("/");
    cy.get('[data-testid="cookie-consent-button"]').then((btn) => { if (btn) {
        btn.click();
    } else {
        cy.visit("/");
    }
    });
})

it('should show greeting message on index page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Willkommen bei der Prüfungsabfrage der Uni Luzern")
    })
})

it('should open swissuniversities.ch on click', async () => {
    cy.intercept({
        method: 'GET',
        url: 'https://www.swissuniversities.ch/'
    }).as('redirectRequest');

    cy.get("footer").find("img").parent()
        .should('have.attr', 'href', 'https://www.swissuniversities.ch/')
        .and('have.attr', 'target', '_blank')
        .invoke('attr', 'target', '_self')
        .click()
    cy.wait('@redirectRequest').its('response.statusCode').should('eq', 200);
})

it('should display header on index page', () => {
    cy.get('#logo').should("be.visible");
})