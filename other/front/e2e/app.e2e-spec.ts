import { MonitorFrontPage } from './app.po';

describe('monitor-front App', function() {
  let page: MonitorFrontPage;

  beforeEach(() => {
    page = new MonitorFrontPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
