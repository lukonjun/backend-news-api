CREATE INDEX idx_published_at ON article (published_at);
CREATE INDEX idx_paywall_article ON article (paywall_article);
CREATE INDEX idx_rss_feed_id ON article (rss_feed_id);
CREATE INDEX idx_count_ratings ON article (count_ratings);