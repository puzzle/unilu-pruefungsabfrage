beforeEach(() => {
    cy.visit("/contact");
})

describe('Verify content of contact page', () => {
    it('should display title on contact page', () => {
        cy.get('body').then((body) => {
            expect(body).to.contain("Kontakt")
        })
    });

    it('should display help text on contact page', () => {
        cy.getByTestId('exam-download-question-text').then((body) => {
            const text = body.text().trim().replace(/\s+/g, ' ');
            expect(text).to.contain("Falls Sie Fragen zum Prüfungsdownload haben, melden Sie sich bitte bei der Prüfungsadministration der Rechtswissenschaftlichen Fakultät.");
        });
    });

    it('should display contact data on contact page', () => {
        cy.get('body').then((body) => {
            expect(body).to.contain("Universität Luzern")
            expect(body).to.contain("Rechtwissenschaftliche Fakultät")
            expect(body).to.contain("Prüfungsadministration")
            expect(body).to.contain("Frohburgstrasse 3, Raum 4.A06")
            expect(body).to.contain("Postfach")
            expect(body).to.contain("6002 Luzern")
            expect(body).to.contain("T +41 41 229 53 14 / 15")
            cy.getByTestId("mail-contact-link").should('exist')
        })
    });
});

describe('Verify functionality of contact page', () => {
    it('should have mailto to pruefungen-rf@unilu.ch', () => {
        cy.getByTestId("mail-contact-link")
            .should('have.attr', 'href')
            .and('include', 'mailto:pruefungen-rf@unilu.ch');
    });
});
