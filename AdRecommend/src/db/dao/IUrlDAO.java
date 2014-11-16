package db.dao;

import db.bean.UrlItem;

import java.util.List;

public interface IUrlDAO {
    public List getUrls();
    public void insertUrl(UrlItem urlItem);
    public UrlItem getItemByUrl(String url);
    public void cleanTable();
}
