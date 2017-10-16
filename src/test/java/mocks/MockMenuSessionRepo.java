package mocks;

import repo.MenuSessionRepo;
import repo.SerializableMenuSession;

/**
 * Created by willpride on 12/7/16.
 */
public abstract class MockMenuSessionRepo implements MenuSessionRepo {
    private SerializableMenuSession serializableMenuSession = new SerializableMenuSession();

    @Override
    public SerializableMenuSession findOneWrapped(String id) {
        return serializableMenuSession;
    }

    @Override
    public SerializableMenuSession save(SerializableMenuSession toBeSaved) {
        serializableMenuSession.setId(toBeSaved.getId());
        serializableMenuSession.setCommcareSession(toBeSaved.getCommcareSession());
        serializableMenuSession.setUsername(toBeSaved.getUsername());
        serializableMenuSession.setDomain(toBeSaved.getDomain());
        serializableMenuSession.setAppId(toBeSaved.getAppId());
        serializableMenuSession.setInstallReference(toBeSaved.getInstallReference());
        serializableMenuSession.setLocale(toBeSaved.getLocale());
        return serializableMenuSession;
    }

    @Override
    public void delete(SerializableMenuSession session) {
        serializableMenuSession = new SerializableMenuSession();
    }

}
